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

import scala.concurrent.Future

import org.apache.pekko.stream.Materializer
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Application, Configuration, Mode, inject}
import uk.gov.hmrc.apiplatform.modules.common.utils.HmrcSpec
import uk.gov.hmrc.http.HttpResponse

import uk.gov.hmrc.internallicensingmanagementapistubs.connectors.ILMSConnector

class StubControllerSpec extends HmrcSpec with GuiceOneAppPerSuite {

  private val stubConfig = Configuration(
    "internal-licensing-management-api.bearerToken" -> "TESTTOKEN"
  )

  val connectorMock = mock[ILMSConnector]

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(stubConfig)
      .bindings(inject.bind(classOf[ILMSConnector]).to(connectorMock))
      .in(Mode.Test)
      .build()

  implicit def mat: Materializer = app.injector.instanceOf[Materializer]

  "PUT" should {
    "return 200 when a file in resources" in {
      when(connectorMock.sendMessage(*, *)(*)).thenReturn(Future.successful(HttpResponse(200)))
      val result = doPut("/cds/lic01/v1", "GBOIL123457")
      status(result) shouldBe Status.OK
    }

    "return 201 when a file in resources" in {
      when(connectorMock.sendMessage(*, *)(*)).thenReturn(Future.successful(HttpResponse(200)))
      val result = doPut("/cds/lic01/v1", "GBSIL0000003D")
      status(result) shouldBe Status.CREATED
    }

    "return 400 when a file in resources" in {
      when(connectorMock.sendMessage(*, *)(*)).thenReturn(Future.successful(HttpResponse(200)))
      val result = doPut("/cds/lic01/v1", "GBOIL123456")
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return 403 when a file in resources" in {
      when(connectorMock.sendMessage(*, *)(*)).thenReturn(Future.successful(HttpResponse(200)))
      val result = doPut("/cds/lic01/v1", "GBSIL0000005F")
      status(result) shouldBe Status.FORBIDDEN
    }

    "return 400 for no file found" in {
      when(connectorMock.sendMessage(*, *)(*)).thenReturn(Future.successful(HttpResponse(200)))
      val result = doPut("/cds/lic01/v1", "GBOIL000000")
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  def doPut(uri: String, ref: String): Future[Result] = {
    val fakeRequest = FakeRequest(PUT, uri).withBody(Json.parse(s"""{"licenceRef":"$ref"}"""))
    route(app, fakeRequest).get
  }
}
