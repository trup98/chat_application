import {BrowserRouter, useRoutes} from "react-router-dom";
import routes from "./routes/routes";
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import {ToastContainer} from "react-toastify";

function App() {
    return (
        <BrowserRouter>
            <ToastContainer/>
            <AppRouter/>
        </BrowserRouter>
    );
}

function AppRouter() {
    let element = useRoutes(routes);
    return element;
}

export default App;
