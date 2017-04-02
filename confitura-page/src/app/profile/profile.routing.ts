import {Routes, RouterModule} from "@angular/router";
import {ModuleWithProviders} from "@angular/core";
import {ProfileEditComponent} from "./profile-edit/profile-edit.component";
import {ProfileViewComponent} from "./profile-view/profile-view.component";
const appRoutes: Routes = [
    { path: 'profile', component: ProfileViewComponent },
    { path: 'profile/edit', component: ProfileEditComponent },
    { path: 'profile/:id', component: ProfileViewComponent },
    { path: 'profile/:id/edit', component: ProfileEditComponent },

];
export const routing: ModuleWithProviders = RouterModule.forChild(appRoutes);