import { AuthenticationDetails, CognitoUser, CognitoRefreshToken } from 'amazon-cognito-identity-js';
import userpool from '../userpool';
export const authenticate=(Email,Password)=>{
    return new Promise((resolve,reject)=>{
        const user=new CognitoUser({
            Username:Email,
            Pool:userpool
        });

        const authDetails= new AuthenticationDetails({
            Username:Email,
            Password
        });

        user.authenticateUser(authDetails,{
            onSuccess:(result)=>{
                console.log("login successful");
                resolve(result);
            },
            onFailure:(err)=>{
                console.log("login failed",err);
                reject(err);
            }
        });
    });
};

export const logout = () => {
    const user = userpool.getCurrentUser();
    user.signOut();
    window.location.href = '/';
};

export const getNick = () => {
    var cognitoUser = userpool.getCurrentUser();
    return cognitoUser.getUsername();
}

export const refreshSession = () => {
    var cognitoUser = userpool.getCurrentUser();
    
    var refreshToken = new CognitoRefreshToken({ RefreshToken: localStorage.getItem('refresh')})
  
    cognitoUser.getSession(function(err, session) {
      localStorage.setItem('token', session.accessToken.jwtToken);
        if (err) {                
          console.log(err);
        }
        else {
          if (!session.isValid()) {
            /* Session Refresh */
            cognitoUser.refreshSession(refreshToken, (err, session) => {
              if (err) { //throw err;
                  console.log('In the err' + err);
              }
              else {
                  localStorage.setItem('token', session.accessToken.jwtToken);
              }
            });   
          }
        }
      });
}
