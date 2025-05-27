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

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

import controllers.Assets

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton()
class StubController @Inject() (assets: Assets, cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  private val statuses: Map[String, Int] = List(
    "GBOIL123456" -> 400
  ).toMap

  def stub(ref: String): Action[AnyContent] = Action.async { implicit request =>
    val default  =
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
    val fullPath = s"/stubs/"
    val file     = s"$ref.json"
    assets.at(fullPath, file)(request).map { result =>
      if (result.header.status == 404) BadRequest(Json.parse(default))
      else new Result(result.header.copy(status = statuses.getOrElse(ref, 200)), result.body)
    }
  }
}
