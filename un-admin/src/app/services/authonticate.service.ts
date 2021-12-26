import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthonticateService {
  isLoggedIn;
  getuserStaus(): boolean{
    //return this.isLoggedIn;
    return true;
  }

  constructor() { }
}
