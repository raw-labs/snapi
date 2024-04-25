package raw.creds.oauth2.api

import java.time.Instant

final case class RenewedAccessToken(
    accessToken: String,
    expiresBy: Instant,
    scopes: Seq[String],
    refreshToken: Option[String],
    header: Option[String] = None
)
