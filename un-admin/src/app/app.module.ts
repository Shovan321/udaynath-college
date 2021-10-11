import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { DropdownModule } from 'primeng/dropdown';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { TabMenuModule } from 'primeng/tabmenu';
import { EmployeeComponent } from './employee/employee.component';
import { ButtonModule } from 'primeng/button';
import { DepartmentComponent } from './department/department.component';
import { HttpClientModule } from '@angular/common/http'
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { AssignRoomComponent } from './assign-room/assign-room.component';
import { EnrollRoomComponent } from './enroll-room/enroll-room.component';
import {AccordionModule} from 'primeng/accordion';
import {CheckboxModule} from 'primeng/checkbox';
import {PanelModule} from 'primeng/panel';
import {BadgeModule} from 'primeng/badge';

const modules = [
  BrowserModule,
  AppRoutingModule,
  DropdownModule,
  BrowserAnimationsModule,
  FormsModule,
  ToastrModule.forRoot(),
  TabMenuModule,
  ButtonModule,
  HttpClientModule,
  ConfirmDialogModule,
  AccordionModule,
  CheckboxModule,
  PanelModule,
  BadgeModule
];
@NgModule({
  declarations: [
    AppComponent,
    EmployeeComponent,
    DepartmentComponent,
    AssignRoomComponent,
    EnrollRoomComponent
  ],
  imports: [
    modules
  ],
  providers: [ConfirmationService],
  bootstrap: [AppComponent]
})
export class AppModule { }
