import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Header, Icon, Segment } from 'semantic-ui-react'
import { Link } from 'react-router-dom';

interface ComponentProps {
    headerTitle: string;
    headerSubtitle?: string;
}

class MainHeaderComponent extends Component<ComponentProps> {

    public render(): ReactNode {
        const browserTitle = this.browserTitle();
        const headerTitle = this.headerTitle();

        document.title = browserTitle;

        return (
            <Segment basic>
                <Header as='h1'>
                    <Link className="header-link" to="/">
                        <Icon name='cubes' />{headerTitle}
                    </Link>
                </Header>
            </Segment>
        );
    }

    private browserTitle() {
        const { headerTitle, headerSubtitle } = this.props;
        if (headerSubtitle) {
            return `${headerSubtitle} - ${headerTitle}`;
        } else {
            return `${headerTitle}`;
        }
    }

    private headerTitle() {
        const { headerTitle, headerSubtitle } = this.props;
        if (headerSubtitle) {
            return `${headerTitle} - ${headerSubtitle}`;
        } else {
            return `${headerTitle}`;
        }
    }
}

export { MainHeaderComponent as MainHeader };