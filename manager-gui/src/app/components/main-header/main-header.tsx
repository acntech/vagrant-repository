import * as React from 'react';
import { Component, ReactNode } from 'react';
import { Header, Icon, Segment } from 'semantic-ui-react'
import { Link } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';

interface ComponentProps {
    headerTitle?: string;
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
        const formattedHeaderTitle = headerTitle ? headerTitle : 'Ouch';
        if (headerSubtitle) {
            return `${formattedHeaderTitle} - ${headerTitle}`;
        } else {
            return formattedHeaderTitle;
        }
    }

    private headerTitle() {
        const { headerTitle, headerSubtitle } = this.props;
        const formattedHeaderTitle = headerTitle ? headerTitle : <FormattedMessage id='mainTitle' defaultMessage='Boo' />;
        if (headerSubtitle) {
            return `${formattedHeaderTitle} - ${headerSubtitle}`;
        } else {
            return formattedHeaderTitle;
        }
    }
}

export { MainHeaderComponent as MainHeader };