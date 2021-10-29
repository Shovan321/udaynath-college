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
import { ConfirmationService, MessageService } from 'primeng/api';
import { AssignRoomComponent } from './assign-room/assign-room.component';
import { EnrollRoomComponent } from './enroll-room/enroll-room.component';
import {AccordionModule} from 'primeng/accordion';
import {CheckboxModule} from 'primeng/checkbox';
import {PanelModule} from 'primeng/panel';
import {BadgeModule} from 'primeng/badge';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MemoComponent } from './memo/memo.component';
import {DividerModule} from 'primeng/divider';
import { HomeComponent } from './home/home.component';

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
  BadgeModule,
  DragDropModule,
  DividerModule,
  
];
@NgModule({
  declarations: [
    AppComponent,
    EmployeeComponent,
    DepartmentComponent,
    AssignRoomComponent,
    EnrollRoomComponent,
    MemoComponent,
    HomeComponent
  ],
  imports: [
    modules
  ],
  providers: [ConfirmationService, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule { }
