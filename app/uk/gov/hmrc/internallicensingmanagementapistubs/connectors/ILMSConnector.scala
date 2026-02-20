/*
 * Copyright 2026 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.HttpClientV2

@Singleton
class ILMSConnector @Inject() (http: HttpClientV2, config: ILMSConfig)(implicit ec: ExecutionContext) {

  def sendMessage(clientId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.put(url"${config.baseUrl}/mdtp/licence/notifyUsage")
      .withBody(Source.fromResource("payload/default.json").getLines().mkString)
      .setHeader(
        "x-client-id"             -> clientId,
        HeaderNames.AUTHORIZATION -> s"Bearer ${config.bearerToken}"
      )
      .execute[HttpResponse]

}
case class ILMSConfig(baseUrl: String, bearerToken: String)
