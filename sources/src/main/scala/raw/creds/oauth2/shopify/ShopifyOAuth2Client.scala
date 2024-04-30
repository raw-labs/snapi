package raw.creds.oauth2.shopify

import raw.creds.oauth2.api.OAuth2Client
import raw.utils.RawSettings

class ShopifyOAuth2Client(implicit settings: RawSettings) extends OAuth2Client {

  override def customHeader: Option[String] = Some("X-Shopify-Access-Token")
}
