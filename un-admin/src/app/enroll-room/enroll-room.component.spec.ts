import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EnrollRoomComponent } from './enroll-room.component';

describe('EnrollRoomComponent', () => {
  let component: EnrollRoomComponent;
  let fixture: ComponentFixture<EnrollRoomComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EnrollRoomComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EnrollRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
