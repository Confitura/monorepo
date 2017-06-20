import {Injectable} from "@angular/core";
@Injectable()
export class ConfirmationService {
    show(message: string): Promise<any> {
        return new Promise((resolve) => {
            swal({
                    title: "Are you sure...",
                    text: message,
                    showCancelButton: true
                },
                () => resolve());

        })
    }
}