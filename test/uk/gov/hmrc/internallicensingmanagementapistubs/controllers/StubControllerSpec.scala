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
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}

class StubControllerSpec extends AnyWordSpec with Matchers with OptionValues with WsScalaTestClient
    with DefaultAwaitTimeout with FutureAwaits with GuiceOneAppPerSuite {
  implicit def mat: Materializer = app.injector.instanceOf[Materializer]

  "GET" should {
    "return 200 when a file in resources" in {
      val result = doGet("/customs/licence/GBOIL123457")
      status(result) shouldBe Status.OK
    }
    "return 400 when a file in resources" in {
      val result = doGet("/customs/licence/GBOIL123456")
      status(result) shouldBe Status.BAD_REQUEST
    }
    "return 400 for no file found" in {
      val result = doGet("/customs/licence/GBOIL000000")
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  def doGet(uri: String): Future[Result] = {
    val fakeRequest = FakeRequest(GET, uri)
    route(app, fakeRequest).get
  }
}
