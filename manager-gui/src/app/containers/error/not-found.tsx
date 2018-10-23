import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Icon } from 'semantic-ui-react';

class NotFoundContainer extends Component {
    public render(): ReactNode {
        return (
            <div className="error error-not-found">
                <h3><Icon name='blind' />Page not found</h3>
            </div>
        );
    }
}

export { NotFoundContainer };