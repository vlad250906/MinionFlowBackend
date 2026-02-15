POST /api/auth/login
body: {email/username, password}
response: {refreshJWT, accessJWT}

POST /api/auth/refresh
refreshJWT as cookie
response: {refreshJWT_new, accessJWT_new}

POST /api/auth/logout
refreshJWT as cookie

POST /api/auth/logout-all
refreshJWT as cookie

POST /api/account/register
body: {email, password, ?}
response: {accountId, activationRequired}

POST /api/account/{accountId}/activate
query params: activationToken

POST /api/account/{accountId}/passwordChange
accessJWT in header
body: {oldPassword, newPassword}

POST /api/account/{accountId}/usernameChange
accessJWT in header
body: {newUsername}

/api/recovery/begin
body: {email}

/api/recovery/{accountId}
body: {activationToken, newPassword}