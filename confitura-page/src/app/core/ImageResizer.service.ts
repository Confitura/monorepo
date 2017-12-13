import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';

@Injectable()
export class ImageResizer {
  applyResizing(url: string, width = 350): string {
    if (environment.production && url !== null) {
      return url.replace('photos', `photos/${width}`);
    } else {
      return url;
    }
  }
}
