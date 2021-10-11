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
}
