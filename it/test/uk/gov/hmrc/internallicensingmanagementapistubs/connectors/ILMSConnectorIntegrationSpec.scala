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

package uk.gov.hmrc.internallicensingmanagementapistubs.connectors

import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Mode}
import uk.gov.hmrc.http.HeaderCarrier

class ILMSConnectorIntegrationSpec extends BaseConnectorIntegrationSpec with GuiceOneAppPerSuite {

  private val stubConfig = Configuration(
    "microservice.services.internal-licensing-management-api.port" -> stubPort,
    "internal-licensing-management-api.bearerToken"                -> "TESTTOKEN"
  )

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(stubConfig)
      .in(Mode.Test)
      .build()

  implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders("x-client-id" -> "CLIENT_ID")
  val underTest                  = app.injector.instanceOf[ILMSConnector]
  val clientId                   = "123-456-789"
  "send" should {
    "pass back a success" in {
      stubPut("/mdtp/licence/notifyUsage")
      val result = await(underTest.sendMessage(clientId, "NOTFOUND"))
      result.status shouldBe Status.OK
    }

    "pass back an error" in {
      stubPut("/mdtp/licence/notifyUsage", Status.BAD_REQUEST)
      val result = await(underTest.sendMessage(clientId, "NOTFOUND"))
      result.status shouldBe Status.BAD_REQUEST
    }
  }
}
