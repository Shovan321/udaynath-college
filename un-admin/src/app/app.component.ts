import { Component } from '@angular/core';
import { NotificationService } from './notification.service'
import {MenuItem} from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'un-admin';
  constructor() {
  }
  items: MenuItem[];

  ngOnInit() {
    this.items = [
        {label: 'Home', icon: 'pi pi-fw pi-home'},
        {label: 'Block', icon: 'pi pi-fw pi-users', routerLink: ['department']},
        /* {label: 'Employee', icon: 'pi pi-fw pi-users', routerLink: ['employee']}, */
        {label: 'Room', icon: 'pi pi-fw pi-users', routerLink: ['assign-room']},
        {label: 'Enroll Room', icon: 'pi pi-fw pi-users', routerLink: ['enroll-room']},
        {label: 'Memo', icon: 'pi pi-fw pi-users', routerLink: ['memo']},
    ];
}
}
