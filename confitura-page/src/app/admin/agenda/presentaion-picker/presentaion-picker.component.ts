import {Component, ElementRef, OnInit} from '@angular/core';
import {PresentationService} from '../../../profile/shared/presentation.service';
import {Presentation} from '../../../profile/shared/presentation.model';

@Component({
  templateUrl: './presentaion-picker.component.html',
  selector: 'cf-presentation-picker'
})
export class PresentationPickerComponent implements OnInit {

  presenations: Presentation[];
  el: any;
  callback: (pres: Presentation) => void;

  constructor(el: ElementRef, private presentationService: PresentationService) {
    this.el = el;
  }

  ngOnInit(): void {
    this.presentationService.getAll().subscribe(it => this.presenations = it);
  }

  show(callback: (pres: Presentation) => void) {
    this.callback = callback;
    $(this.el.nativeElement.firstChild).modal({show: true});
  }

  select(pres: Presentation) {
    this.callback(pres);
    $(this.el.nativeElement.firstChild).modal('hide');
  }
}
