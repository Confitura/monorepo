import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {PartnerEditComponent} from "./partners/partner/partner-edit.component";
import {PartnerComponent} from "../pages/partners/partner/partner.component";
import {IsAdminGuard} from "./is-admin-guard.service";
import {LoginComponent} from "../pages/login/login.component";
import {UserListComponent} from "./users/user-list/user-list.component";
const routes: Routes = [
    {
        path: "admin2",
        children: [
            {
                path: "",
                pathMatch: "full",
                component: LoginComponent,
            },
            {
                path: "",
                canActivateChild: [IsAdminGuard],
                children: [
                    {path: "users", component: UserListComponent},
                    {path: "partners/add", component: PartnerEditComponent},
                    {path: "partners/edit/:id", component: PartnerEditComponent},
                    {path: "partners/:id", component: PartnerComponent},]
            }
        ]
    }
];
@NgModule({
    imports: [RouterModule.forChild(routes)],
    providers: [IsAdminGuard]

})
export class AdminRoutingModule {

}