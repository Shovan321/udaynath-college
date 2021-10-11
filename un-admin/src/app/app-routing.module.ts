import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AssignRoomComponent } from './assign-room/assign-room.component';
import { DepartmentComponent } from './department/department.component';
import { EmployeeComponent } from './employee/employee.component';
import { EnrollRoomComponent } from './enroll-room/enroll-room.component';

const routes: Routes = [
  {
    path:'employee',
    component:EmployeeComponent
  },{
    path:'department',
    component: DepartmentComponent
  },{
    path:'assign-room',
    component: AssignRoomComponent
  },
  {
    path:'enroll-room',
    component: EnrollRoomComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
