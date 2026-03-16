/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.internallicensingmanagementapistubs.controllers

import java.io.FileNotFoundException
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import scala.io.Source

import controllers.Assets

import play.api.libs.json.Json
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import uk.gov.hmrc.internallicensingmanagementapistubs.connectors.ILMSConnector

case class ILMSRequest(licenceRef: String)

object ILMSRequest {
  implicit val format = Json.format[ILMSRequest]
}

@Singleton()
class StubController @Inject() (assets: Assets, ilmsConnector: ILMSConnector, cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def stub(): Action[ILMSRequest] = Action.async(controllerComponents.parsers.json[ILMSRequest]) { implicit request =>
    val ref     = request.body.licenceRef
    val default =
      s"""{
         |  "correlationId": "123e4567-e89b-12d3-a456-426655440000",
         |  "sourceSystem": "CDS",
         |  "destinationSystem": "ILBDOTI",
         |  "licenceRef": "$ref",
         |  "result": "REJECTED",
         |  "errors": [
         |    {
         |      "code": "LIC.ERR.02",
         |      "message": "Duplicate Foreign Traders not allowed",
         |      "path": "/foreignTrader[1]/name"
         |    }
         |  ]
         |}""".stripMargin
    val file    = s"stubs/$ref.json"
    val result  =
      try {
        val body = Json.parse(Source.fromResource(file).getLines().mkString)

        ref match {
          case "GBSIL0000005F"                                     => Forbidden(body)
          case "GBSIL0000003D" | "GBSIL0000002C" | "GBSIL0000001B" => Created(body)
          case _                                                   => Ok(body)
        }
      } catch {
        case _: FileNotFoundException => BadRequest(Json.parse(default))
      }
    ilmsConnector.sendMessage(request.headers.get("x-client-id").getOrElse(""), ref).map(_ => result)
  }
}
