import {Component, OnInit} from "@angular/core";
import {Observable} from "rxjs";
import {Presentation} from "../../pages/profile/presentation/presentation.model";
import {User} from "../../pages/profile/user.model";
import {UserService} from "../../pages/profile/user.service";
import {CurrentUser} from "../../security/current-user.service";
import {PresentationService} from "../../pages/profile/presentation/presentation.service";
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