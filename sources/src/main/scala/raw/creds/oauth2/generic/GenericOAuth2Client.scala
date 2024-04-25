package raw.creds.oauth2.generic

import raw.creds.oauth2.api.OAuth2Client
import raw.utils.RawSettings

class GenericOAuth2Client(implicit settings: RawSettings) extends OAuth2Client {
  // Just uses the default OAuth2Client implementation.
}
