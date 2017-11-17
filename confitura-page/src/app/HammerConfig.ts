import {HammerGestureConfig} from '@angular/platform-browser';

declare let Hammer: any;

export class HammerConfig extends HammerGestureConfig {
  buildHammer(element: HTMLElement) {
    return new Hammer(element, {
      touchAction: 'pan-y'
    });
  }
}
