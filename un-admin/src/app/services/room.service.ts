import { Injectable } from '@angular/core';
import { Room } from '../model/room';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class RoomService {


  constructor(private apiService: ApiService) { }

  private path = 'room'
  create(room: Room) {
    return this.apiService.post(this.path, room);
  }

  findAll() {
    return this.apiService.get(this.path);
  }

  findByBlockIds(selectedIds: any[]) {
    return this.apiService.get(this.path+'/'+selectedIds);
  }
}
