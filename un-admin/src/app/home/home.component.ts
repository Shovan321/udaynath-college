import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthonticateService } from '../services/authonticate.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private authonticateService : AuthonticateService,
    private router : Router) { }
  imagepath='assets/images/home.png;'

  username;
  password;
  ngOnInit(): void {
    
  }

  login(){
    if(this.username && this.password && this.username==='unadmin' && this.password === 'un@123'){
      this.authonticateService.isLoggedIn = true;
      this.router.navigateByUrl('/enroll-room');
    } else{
      this.authonticateService.isLoggedIn = false;
    }
  }
  reset(){
    this.username ='';
    this.password =''
  }

}
