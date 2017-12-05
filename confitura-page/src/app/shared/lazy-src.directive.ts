import {Directive, HostBinding, Input, OnChanges, SimpleChanges} from '@angular/core';
import * as lozad from 'lozad';

@Directive({
  selector: '[cfLazySrc]'
})
export class LazySrcDirective implements OnChanges {


  @Input()
  cfLazySrc: string;

  @HostBinding('attr.data-src')
  dataSrc;

  @HostBinding('class.lozad')
  lozad = true;

  constructor() {

  }

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSrc = this.cfLazySrc;
    setTimeout(() => {
      lozad().observe();
    }, 0);
  }


}
