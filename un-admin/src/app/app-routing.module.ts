import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AssignRoomComponent } from "./assign-room/assign-room.component";
import { DepartmentComponent } from "./department/department.component";
import { EmployeeComponent } from "./employee/employee.component";
import { EnrollRoomComponent } from "./enroll-room/enroll-room.component";
import { HomeComponent } from "./home/home.component";
import { MemoComponent } from "./memo/memo.component";
import { AuthguardGuard } from "./services/authguard.guard";

const routes: Routes = [
  {
    path: "home",
    component: HomeComponent,
  },
  {
    path: "employee",
    component: EmployeeComponent,
    canActivate: [AuthguardGuard],
  },
  {
    path: "department",
    component: DepartmentComponent,
    canActivate: [AuthguardGuard],
  },
  {
    path: "assign-room",
    component: AssignRoomComponent,
    canActivate: [AuthguardGuard],
  },
  {
    path: "enroll-room",
    component: EnrollRoomComponent,
    canActivate: [AuthguardGuard],
  },
  {
    path: "memo",
    component: MemoComponent,
    canActivate: [AuthguardGuard],
  },
  {
    path: "**",
    redirectTo: "/home",
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
