import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Icon } from 'semantic-ui-react'

class UnknownErrorContainer extends Component {
    public render(): ReactNode {
        return (
            <div className="error error-unknown">
                <h3><Icon name='x' />An unknown error occured</h3>
            </div>
        );
    }
}

export { UnknownErrorContainer };