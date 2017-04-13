import {RouterModule, Routes} from "@angular/router";
import {ModuleWithProviders} from "@angular/core";
import {ProfileEditComponent} from "./profile-edit/profile-edit.component";
import {ProfileViewComponent} from "./profile-view/profile-view.component";
import {PresentationEditComponent} from "./presentation-edit/presentation-edit.component";
import {ProfileCompleteGuard} from "./shared/profile-complete-guard.service";
const appRoutes: Routes = [
    {path: 'profile', component: ProfileViewComponent},
    {path: 'profile/edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard]},
    {path: 'profile/:id/edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard]},
    {path: 'profile/:id', component: ProfileViewComponent},
    {path: 'presentation', component: PresentationEditComponent},
    {path: 'presentation/:id', component: PresentationEditComponent},

];
export const routing: ModuleWithProviders = RouterModule.forChild(appRoutes);