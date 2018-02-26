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
    console.log('ERRORE!!');
    this.element.nativeElement.removeAttribute('src');
  }

  constructor(private element: ElementRef) {
    this.element.nativeElement.removeAttribute('src');
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.element.nativeElement.removeAttribute('src');
    this.element.nativeElement.removeAttribute('data-loaded');
    this.dataSrc = this.cfLazySrc;
    setTimeout(() => lozad().observe(), 0);
  }


}
