import {Login} from "../pages/auth/Login";
import Dashboard from "../pages/Dashboard";

const routes = [

    {path: "/", element: <Login/>},
    {path: "/dashboard", element: <Dashboard/>},

]

export default routes;
