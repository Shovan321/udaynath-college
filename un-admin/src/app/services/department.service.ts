import { Injectable } from '@angular/core';
import { Department} from '../model/department'
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {

  constructor(private apiService : ApiService) { }

  path = 'department'
  create(dept: Department){
    return this.apiService.post(this.path, dept);
  }

  getAll() {
    return this.apiService.get(this.path);
  }

  delete(dept: Department) {
    return this.apiService.delete(this.path+'/'+dept.id);
  }
}
