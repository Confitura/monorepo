import {
    HammerGestureConfig,
    HAMMER_GESTURE_CONFIG,
} from '@angular/platform-browser';

declare var Hammer: any;

export class HammerConfig extends HammerGestureConfig  {
    buildHammer(element: HTMLElement) {
        return  new Hammer(element, {
            touchAction: "pan-y"
        });
    }
}