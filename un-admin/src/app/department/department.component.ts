import { Component, OnInit } from '@angular/core';
import { Department } from '../model/department';
import { UNResponse } from '../model/UNResponse';
import { NotificationService } from '../notification.service';
import { DepartmentService } from '../services/department.service';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-department',
  templateUrl: './department.component.html',
  styleUrls: ['./department.component.css']
})
export class DepartmentComponent implements OnInit {

  dept: Department;
  departments: Department[] = [];
  constructor(private departmentService: DepartmentService,
    private notificationService: NotificationService,
    private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.dept = new Department();
    this.findAll();
  }

  save() {
    this.departmentService.create(this.dept).subscribe((res: Department) => {
      this.notificationService.showSuccess('Successfully saved', 'Department');
      this.reset();
      this.findAll();
    }, error => {
      this.notificationService.showError('Something went wrong', 'Department');
    });
  }
  reset() {
    this.dept = new Department();
  }
  
  select(dept) {
    this.dept = dept;
  }
  deleteConfirm(dept) {
    let self = this;
    this.confirmationService.confirm({
      message: `Are you sure you want to delete <b> ${dept.departmentName} </b>?`,
      accept: () => {
        self.delete(dept);
      }
    });
  }
  delete(dept){
    this.departmentService.delete(dept).subscribe(res => {
      this.notificationService.showSuccess('Successfully deleted', 'Department');
      this.findAll();
    });
  }
  findAll() {
    this.departments = [];
    this.departmentService.getAll().subscribe((res: UNResponse) => {
      this.departments = res.data;
    });
  }
}
