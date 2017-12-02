import {HammerGestureConfig} from '@angular/platform-browser';
import {Injectable} from '@angular/core';

declare let Hammer: any;

@Injectable()
export class HammerConfig extends HammerGestureConfig {
  buildHammer(element: HTMLElement) {
    return new Hammer(element, {
      touchAction: 'pan-y'
    });
  }
}
