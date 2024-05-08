import { Button, TextField} from '@mui/material'
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {CognitoUserAttribute } from 'amazon-cognito-identity-js';


import userpool from '../userpool';

const Signup = () => {

  const Navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [username, setUsername] = useState('');
  const [emailErr, setEmailErr] = useState('');
  const [passwordErr, setPasswordErr] = useState('');
  const [usernameErr, setUsernameErr] = useState('');

  const formInputChange = (formField, value) => {
    if (formField === "email") {
      setEmail(value);
    }
    if (formField === "password") {
      setPassword(value);
    }
    if (formField === "username") {
      setUsername(value);
    }
  };

  const validation = () => {
    return new Promise((resolve,reject)=>{
      if (email === '' && password === '' && username === '') {
        setEmailErr("Email is Required");
        setPasswordErr("Password is required")
        setUsernameErr("Username is required")
        resolve({email:"Email is Required",password:"Password is required",username:"Username is required"});
      }
      else if (email === '') {
        setEmailErr("Email is Required")
        resolve({email:"Email is Required",password:""});
      }
      else if (password === '') {
        setPasswordErr("Password is required")
        resolve({email:"",password:"Password is required"});
      }
      else if (username === '') {
        setPasswordErr("Username is required")
        resolve({email:"",username:"Username is required"});
      }
      else if (password.length < 6) {
        setPasswordErr("must be 6 character")
        resolve({email:"",password:"must be 6 character"});
      }
      else if (username.length < 3) {
        setUsernameErr("must be 3 character")
        resolve({email:"",password:"must be 3 character"});
      }
      else{
        resolve({email:"",password:"",username:""});
      }
      reject('')
    });
  };

  const handleClick = (e) => {
    setEmailErr("");
    setPasswordErr("");
    setUsernameErr("");
    validation()
      .then((res) => {
        if (res.email === '' && res.password === '' && res.username === '') {
          const attributeList = [];
          attributeList.push(
            new CognitoUserAttribute({
              Name: 'email',
              Value: email,
            }),
          );
          userpool.signUp(username, password, attributeList, null, (err, data) => {
            if (err) {
              console.log(err);
              alert("Couldn't sign up");
            } else {
              console.log(data);
              alert('User Added Successfully');
              Navigate('/game');
            }
          });
        }
      }, err => console.log(err))
      .catch(err => console.log(err));
  }

  return (
    <div className="signup">

      <div className='form'>
        <div className="formfield">
          <TextField
            value={email}
            onChange={(e) => formInputChange("email", e.target.value)}
            label="Email"
            helperText={emailErr}
          />
        </div>
        <div className='formfield'>
          <TextField
            value={password}
            onChange={(e) => { formInputChange("password", e.target.value) }}
            type="password"
            label="Password"
            helperText={passwordErr}
          />
        </div>
        <div className="formfield">
          <TextField
            value={username}
            onChange={(e) => { formInputChange("username", e.target.value) }}
            label="Username"
            helperText={usernameErr}
          />
        </div>
        <div className='formfield'>
          <Button type='submit' variant='contained' onClick={handleClick}>Signup</Button>
        </div>
      </div>

    </div>
  )
}

export default Signup