import * as React from 'react';
import { Component, ReactNode } from 'react';

class Home extends Component {

    public render(): ReactNode {
        return (
            <p>Welcome!</p>
        );
    }
}

export { Home as HomeContainer };