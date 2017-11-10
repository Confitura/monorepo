import {Directive, HostBinding, Input, OnInit} from '@angular/core';
import * as lozad from 'lozad';

@Directive({
  selector: '[cfLazySrc]'
})
export class LazySrcDirective implements OnInit {

  @Input()
  cfLazySrc: string;

  @HostBinding('attr.data-src')
  dataSrc;

  @HostBinding('class.lozad')
  lozad = true;

  constructor() {

  }

  ngOnInit(): void {
    this.dataSrc = this.cfLazySrc;
    setTimeout(() => {
      lozad().observe();
    }, 0);
  }


}
