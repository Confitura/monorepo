import {Directive, ElementRef, HostBinding, Input, OnChanges, SimpleChanges} from '@angular/core';
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

  constructor(private element: ElementRef) {
    console.log(element);
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.element.nativeElement.removeAttribute('src');
    this.element.nativeElement.removeAttribute('data-loaded');
    this.dataSrc = this.cfLazySrc;
    setTimeout(() => lozad().observe(), 0);
  }


}
