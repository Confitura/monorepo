import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CospeakersComponent } from './cospeakers.component';

describe('CospeakersComponent', () => {
  let component: CospeakersComponent;
  let fixture: ComponentFixture<CospeakersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CospeakersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CospeakersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
