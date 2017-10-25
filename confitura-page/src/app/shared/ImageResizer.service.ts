import {Injectable} from "@angular/core";

@Injectable()
export class ImageResizer {
    applyResizing(url: string, width = 350): string {
        if (ENV == "prod") {
            return url.replace("photos", `photos/${width}`);
        } else {
            return url;
        }
    }
}