import {Directive, ElementRef, HostBinding, HostListener, Input, OnChanges, SimpleChanges} from '@angular/core';
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

  @HostBinding('src')
  src: string;

  @HostListener('error')
  onerror() {
    this.element.nativeElement.removeAttribute('src');
  }

  constructor(private element: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.dataSrc = this.cfLazySrc;
    setTimeout(() => {
      lozad().observe();
    });
  }


}
