import * as React from 'react';
import { Component, ReactNode } from 'react';

class NotFoundContainer extends Component {
    public render(): ReactNode {
        return (
            <div className="error error-not-found">
                <h1>Not found</h1>
            </div>
        );
    }
}

export { NotFoundContainer };