import {ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {CurrentUser} from "../../security/current-user.service";
import {UserService} from "../../pages/profile/user.service";
import {Observer} from "rxjs";
import {Observable} from "rxjs/Observable";
import {ProfileEditComponent} from "../profile-edit/profile-edit.component";
import {User} from "../../pages/profile/user.model";
@Injectable()
export class ProfileCompleteGuard implements CanDeactivate<ProfileEditComponent> {

    constructor(private currentUser: CurrentUser, private userService: UserService) {
    }

    canDeactivate(component: any, currentRoute: ActivatedRouteSnapshot, currentState: RouterStateSnapshot, nextState?: RouterStateSnapshot): Observable<boolean> {
        return Observable.create((observer: Observer<boolean>) => {
            this.userService.getBy(this.currentUser.get().jti)
                .subscribe(user => {
                        observer.next(this.isValid(user));
                    }
                );
        });
    }

    private isValid(user: User): boolean {
        return user.name != null &&
            user.email != null &&
            user.bio != null;
    }


}