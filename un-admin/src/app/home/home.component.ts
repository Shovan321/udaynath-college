import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { NotificationService } from "../notification.service";
import { AuthonticateService } from "../services/authonticate.service";

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"],
})
export class HomeComponent implements OnInit {
  constructor(
    private authonticateService: AuthonticateService,
    private router: Router,
    private notificationService: NotificationService
  ) {}
  imagepath = "assets/images/home.png;";

  username;
  password;
  ngOnInit(): void {}

  login() {
    if (
      this.username &&
      this.password &&
      this.username === "unadmin" &&
      this.password === "un@P@ssw0rd"
    ) {
      this.authonticateService.isLoggedIn = true;
      this.notificationService.showInfo("User", "successfully logged in!!!");
      this.router.navigateByUrl("/enroll-room");
    } else {
      this.notificationService.showError(
        "User",
        "Please check user id and password!!!"
      );
      this.username = "";
      this.password = "";
      this.authonticateService.isLoggedIn = false;
    }
  }
  reset() {
    this.username = "";
    this.password = "";
  }

  isLoggedIn(){
    return this.authonticateService.isLoggedIn;
  }
}
