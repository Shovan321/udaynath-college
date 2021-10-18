import { Injectable } from '@angular/core';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private apiService : ApiService) { }

  path='student';
  upload(fileList){
    return this.apiService.postFile(this.path, fileList);
  }
  uploadForInvesiloter(path,fileList){
    return this.apiService.postFile(path, fileList);
  }
  manageRoomAndInvesiloter(data : any, rooms, sizeOfInvesiloter){
    let obj = {
      'alertsRooms': data,
      'selectedRooms': rooms,
      'invesiloterSize':sizeOfInvesiloter
    }
    return this.apiService.post("invesiloters/manage-invesiloter", obj);
  }
}
