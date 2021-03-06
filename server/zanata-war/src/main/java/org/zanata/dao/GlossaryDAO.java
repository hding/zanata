/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.dao;

import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.zanata.common.LocaleId;
import org.zanata.hibernate.search.DefaultNgramAnalyzer;
import org.zanata.model.HGlossaryEntry;
import org.zanata.model.HGlossaryTerm;
import org.zanata.webtrans.shared.rpc.GetGlossary.SearchType;

/**
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 *
 **/
@Name("glossaryDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class GlossaryDAO extends AbstractDAOImpl<HGlossaryEntry, Long>
{
   @In
   private FullTextEntityManager entityManager;

   public GlossaryDAO()
   {
      super(HGlossaryEntry.class);
   }

   public GlossaryDAO(Session session)
   {
      super(HGlossaryEntry.class, session);
   }

   public HGlossaryEntry getEntryById(Long id)
   {
      return (HGlossaryEntry) getSession().createCriteria(HGlossaryEntry.class).add(Restrictions.naturalId().set("id", id)).setCacheable(true).setComment("GlossaryDAO.getEntryById").uniqueResult();
   }

   @SuppressWarnings("unchecked")
   public List<HGlossaryEntry> getEntriesByLocaleId(LocaleId locale)
   {
      Query query = getSession().createQuery("from HGlossaryEntry as e WHERE e.id IN (SELECT t.glossaryEntry.id FROM HGlossaryTerm as t WHERE t.locale.localeId= :localeId)");
      query.setParameter("localeId", locale);
      return query.list();
   }

   @SuppressWarnings("unchecked")
   public List<HGlossaryEntry> getEntries()
   {
      Query query = getSession().createQuery("from HGlossaryEntry");
      return query.list();
   }

   public HGlossaryTerm getTermByEntryAndLocale(Long glossaryEntryId, LocaleId locale)
   {
      Query query = getSession().createQuery("from HGlossaryTerm as t WHERE t.locale.localeId= :locale AND glossaryEntry.id= :glossaryEntryId");
      query.setParameter("locale", locale);
      query.setParameter("glossaryEntryId", glossaryEntryId);
      return (HGlossaryTerm) query.uniqueResult();
   }

   @SuppressWarnings("unchecked")
   public List<HGlossaryTerm> getTermByGlossaryEntryId(Long glossaryEntryId)
   {
      Query query = getSession().createQuery("from HGlossaryTerm as t WHERE t.glossaryEntry.id= :glossaryEntryId");
      query.setParameter("glossaryEntryId", glossaryEntryId);
      return query.list();
   }

   /* @formatter:off */
   public HGlossaryEntry getEntryBySrcLocaleAndContent(LocaleId localeid, String content)
   {
      Query query = getSession().createQuery("from HGlossaryEntry as e " + 
            "WHERE e.srcLocale.localeId= :localeid AND e.id IN " + 
            "(SELECT t.glossaryEntry.id FROM HGlossaryTerm as t " + 
            "WHERE t.locale.localeId=e.srcLocale.localeId " + 
            "AND t.content= :content)");
      query.setParameter("localeid", localeid);
      query.setParameter("content", content);
      return (HGlossaryEntry) query.uniqueResult();
   }

   public List<Object[]> getSearchResult(String searchText, SearchType searchType, List<Long> termIds, final int maxResult) throws ParseException
   {
      String queryText;
      switch (searchType)
      {
      case FUZZY:
         // search by N-grams
         queryText = QueryParser.escape(searchText);
         break;

      case EXACT:
         queryText = "\"" + QueryParser.escape(searchText) + "\"";
         break;

      default:
         throw new RuntimeException("Unknown query type: " + searchType);
      }

      QueryParser parser = new QueryParser(Version.LUCENE_29, "content", new DefaultNgramAnalyzer(4));
      org.apache.lucene.search.Query textQuery = parser.parse(queryText);
      FullTextQuery ftQuery = entityManager.createFullTextQuery(textQuery, HGlossaryTerm.class);
      ftQuery.enableFullTextFilter("glossaryFilter").setParameter("termIds", termIds);
      ftQuery.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
      @SuppressWarnings("unchecked")
      List<Object[]> matches = ftQuery.setMaxResults(maxResult).getResultList();
      return matches;
   }
}


 