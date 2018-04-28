import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {FaqComponent} from './faq.component';
import {PageService} from '../../shared/page/page.service';
import {Component} from '@angular/core';

@Component({
  selector: 'cf-page-header',
  template: ''
})
class PageHeaderMockComponent {
}

@Component({
  selector: 'cf-page',
  template: ''
})
class PageMockComponent {
}


describe('FaqComponent', () => {
  let component: FaqComponent;
  let fixture: ComponentFixture<FaqComponent>;

  beforeEach(async(() => {
    const spy = jasmine.createSpyObj('PageService', ['get', 'save']);
    TestBed.configureTestingModule({
      declarations: [FaqComponent, PageHeaderMockComponent, PageMockComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
