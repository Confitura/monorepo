import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {V4pComponent} from "./v4p.component";
import {V4pStartComponent} from "./start/v4p-start.component";
import {V4pEndComponent} from "./end/v4p-end.component";
const routes: Routes = [
    {
        path: "v4p/voting",
        component: V4pComponent
    },{
        path: "v4p",
        component: V4pStartComponent
    },{
        path: "v4p/end",
        component: V4pEndComponent
    },
];
@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class V4pRoutingModule {

}