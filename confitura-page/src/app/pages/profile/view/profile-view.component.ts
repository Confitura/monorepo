import {Component, OnInit} from "@angular/core";
import {UserService} from "../user.service";
import {CurrentUser} from "../../../security/current-user.service";
import {User} from "../user.model";
import {Observable} from "rxjs";
import {Presentation} from "../presentation/presentation.model";
import {PresentationService} from "../presentation/presentation.service";
@Component({
    templateUrl: "./profile-view.component.html"
})
export class ProfileViewComponent implements OnInit {

    model: User;
    presentations: Observable<Presentation[]>;

    constructor(private service: UserService, private currentUser: CurrentUser, private presentationService: PresentationService) {

    }

    ngOnInit(): void {
        this.reload();
    }

    reload() {
        this.service.getBy(this.currentUser.get().jti)
            .subscribe(user => {
                this.model = user;
                user.photo += "?v=" + new Date().getMilliseconds();
            });
        this.presentations = this.presentationService.getAllFor(this.currentUser.get().jti);
    }

    remove(presentation:Presentation){
        this.presentationService.remove(presentation)
            .subscribe(()=> this.reload());
    }


}