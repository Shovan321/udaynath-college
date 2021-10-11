import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignRoomComponent } from './assign-room.component';

describe('AssignRoomComponent', () => {
  let component: AssignRoomComponent;
  let fixture: ComponentFixture<AssignRoomComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssignRoomComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
