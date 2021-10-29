import { Component, OnInit } from '@angular/core';
import { Department } from '../model/department';
import { DepartmentService } from '../services/department.service';
import { RoomService } from '../services/room.service';
import {Room} from '../model/room';
import { NotificationService } from '../notification.service';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-assign-room',
  templateUrl: './assign-room.component.html',
  styleUrls: ['./assign-room.component.css']
})
export class AssignRoomComponent implements OnInit {
  public departmentList = [];
  selectedDepartment: Department;
  room: Room = new Room();
  rooms = [];
  constructor(private roomService: RoomService,
    private departmentService : DepartmentService,
    private confirmationService : ConfirmationService,
    private notifyService : NotificationService) { }

  ngOnInit(): void {
    this.room = new Room();
    this.rooms = [];
    this.selectedDepartment = new Department();
    this.departmentService.getAll().subscribe(res => {
      this.departmentList = res['data'];
      this.selectedDepartment = this.departmentList[0].id;
    });
    this.findAll();
  }
  reset(){
    this.room = new Room();
  }
  findAll(){
    this.roomService.findAll().subscribe(res => {
      this.rooms = res['data'];
    });
  }
  save(){
    if(!this.room.deptId){
      this.room.deptId = this.departmentList[0].id;
    }
    this.roomService.create(this.room).subscribe(res => {
      this.notifyService.showSuccess("Successfully saved", "Room");
      this.reset();
      this.findAll();
    });
  }
  select(data){
    this.room = data;
  }
  checkreadOnly(room){
    if(room.id){
      return true;
    } else{
      return false;
    }
  }
  deleteConfirm(room){
    let self = this;
    this.confirmationService.confirm({
      message: `Are you sure you want to delete <b> ${room.name} </b>?`,
      accept: () => {
        self.delete(room);
      }
    });
  }
  delete(room){
    this.roomService.delete(room.id).subscribe(res => {
      this.findAll();
    });
  }
}
