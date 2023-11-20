package raw.creds

import raw.creds.api._
import raw.utils.AuthenticatedUser

import scala.collection.mutable

class TestCredentialsService extends CredentialsService {

  private val storedCredentials = new mutable.HashMap[(AuthenticatedUser, String), Credential]()

  override protected def doRegisterS3Bucket(user: AuthenticatedUser, bucket: S3Bucket): Boolean = {
    if (storedCredentials.contains(user, bucket.name)) {
      false
    } else {
      storedCredentials.put((user, bucket.name), bucket)
      true
    }
  }

  override def getS3Bucket(user: AuthenticatedUser, name: String): Option[S3Bucket] = {
    storedCredentials.get((user, name)).collect { case s3Bucket: S3Bucket => s3Bucket }
  }

  override def listS3Buckets(user: AuthenticatedUser): List[String] = {
    storedCredentials.collect { case (_, s3Bucket: S3Bucket) => s3Bucket.name }.toList
  }

  override def unregisterS3Bucket(user: AuthenticatedUser, name: String): Boolean = {
    val maybeBucket = getS3Bucket(user, name)
    maybeBucket.foreach(_ => storedCredentials.remove((user, name)))
    maybeBucket.isDefined
  }

  // TODO: implement the rest of the methods
  override def registerDropboxToken(user: AuthenticatedUser, dropboxToken: DropboxToken): Boolean = ???

  override def getDropboxToken(user: AuthenticatedUser): Option[DropboxToken] = ???

  override def unregisterDropboxToken(user: AuthenticatedUser): Boolean = ???

  override protected def doRegisterNewHttpCredential(
      user: AuthenticatedUser,
      name: String,
      token: NewHttpCredential
  ): Boolean = {
    false
  }

  override def getNewHttpCredential(user: AuthenticatedUser, name: String): Option[NewHttpCredential] = ???

  override def unregisterNewHttpCredential(user: AuthenticatedUser, name: String): Boolean = ???
  override def listNewHttpCredentials(user: AuthenticatedUser): List[HttpCredentialId] = ???

  override def getNewHttpAuth(user: AuthenticatedUser, name: String): Option[NewHttpAuth] = ???
  override protected def doRegisterRDBMSServer(
      user: AuthenticatedUser,
      name: String,
      db: RelationalDatabaseCredential
  ): Boolean = ???

  override def getRDBMSServer(user: AuthenticatedUser, name: String): Option[RelationalDatabaseCredential] = ???

  override def listRDBMSServers(user: AuthenticatedUser): List[String] = ???

  override def unregisterRDBMSServer(user: AuthenticatedUser, name: String): Boolean = ???

  override protected def doRegisterHTTPCred(user: AuthenticatedUser, cred: HttpCredential): Boolean = ???

  override def getHTTPCred(user: AuthenticatedUser, url: String): Option[HttpCredential] = ???

  override def listHTTPCreds(user: AuthenticatedUser): List[String] = ???

  override def unregisterHTTPCred(user: AuthenticatedUser, url: String): Boolean = ???

  override def doRegisterSecret(user: AuthenticatedUser, secret: Secret): Boolean = ???

  override def getSecret(user: AuthenticatedUser, name: String): Option[Secret] = ???

  override def listSecrets(user: AuthenticatedUser): List[String] = ???

  override def unregisterSecret(user: AuthenticatedUser, name: String): Boolean = ???

  override def doStop(): Unit = {}
}
